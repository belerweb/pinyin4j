package net.sourceforge.pinyin4j;

import net.sourceforge.pinyin4j.multipinyin.Trie;

/**
 * @author fanxiaopeng
 * @date 2025/7/11 11:26
 * @desciption:
 */
public class GlobalTrie {

    public static Trie trie;


    public static Trie getTrie() {
        if (trie == null) {
            trie = new Trie();
        }
        return trie;
    }
}
