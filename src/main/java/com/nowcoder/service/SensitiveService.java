package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "敏感词";


    private boolean isSymbol(Character c) {
        int ic = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    //读取敏感词文件
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {

            InputStream is = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream("SensitiveWords.txt");

            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);

            String linTxt;

            while ( (linTxt = bufferedReader.readLine()) != null) {
                linTxt = linTxt.trim();
                addWords(linTxt);
            }
            read.close();

        } catch (Exception e) {
            logger.error("读取敏感词文件失败！", e.getMessage() );
        }
    }

    //前缀树
    private class TrieNode {
        private Boolean end = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        Boolean isKeyEnd() {
            return end;
        }

        void setKeyEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCounts() {
            return subNodes.size();
        }

    }

    private TrieNode rootNode = new TrieNode();

    private void addWords(String linTxt) {
        TrieNode tempNode = rootNode;

        for (int i = 0; i < linTxt.length(); i++) {
            Character c = linTxt.charAt(i);

            //如果c是空格，continue
            if ( isSymbol(c) ) {
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);

            //初始化
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == linTxt.length() - 1) {
                //关键词结束
                tempNode.setKeyEnd(true);
            }
        }
    }

    public String filter(String text) {

        if (StringUtils.isBlank(text)) {
            return text;
        }

        String replacement = "***";
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {

            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }

                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if (tempNode == null) {
                result.append(text.charAt(begin));

                begin = begin + 1;
                position = begin;

                tempNode = rootNode;
            } else if (tempNode.isKeyEnd()) {
                result.append(replacement);
                begin = position + 1;
                position = begin;
                tempNode = rootNode;
            } else {
                position ++;
            }
        }

        result.append(text.substring(begin));
        return result.toString();
    }

    public static void main(String[] args) {
        SensitiveService s = new SensitiveService();
        s.addWords("色情");
        s.addWords("强奸");
        System.out.println(s.filter("他要强【】奸多名女大学生， 这个是很色情的事情！"));
    }

}
