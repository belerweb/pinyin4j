package net.sourceforge.pinyin4j.multipinyin;

import java.io.*;
import java.util.Hashtable;

/**
 * Created by 刘一波 on 16/3/4.
 * E-Mail:yibo.liu@tqmall.com
 */
public class Trie {

    private Hashtable<String, Trie> values = new Hashtable<String, Trie>();//本节点包含的值

    private String pinyin;//本节点的拼音

    private Trie nextTire;//下一个节点,也就是匹配下一个字符

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Trie getNextTire() {
        return nextTire;
    }

    public void setNextTire(Trie nextTire) {
        this.nextTire = nextTire;
    }

    /**
     * 加载拼音
     *
     * @param inStream 拼音文件输入流
     * @throws IOException
     */
    public synchronized void load(InputStream inStream) throws IOException {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                String[] keyAndValue = s.split(" ");
                if (keyAndValue.length != 2)
                    continue;
                Trie trie = new Trie();
                trie.pinyin = keyAndValue[1];
                put(keyAndValue[0], trie);
            }
        } finally {
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (bufferedReader != null)
                bufferedReader.close();
        }
    }

    /**
     * 加载多音字拼音词典
     *
     * @param inStream 拼音文件输入流
     */
    public synchronized void loadMultiPinyin(InputStream inStream) throws IOException {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                String[] keyAndValue = s.split(" ");
                if (keyAndValue.length != 2)
                    continue;

                String key = keyAndValue[0];//多于一个字的字符串
                String value = keyAndValue[1];//字符串的拼音
                char[] keys = key.toCharArray();

                Trie currentTrie = this;
                for (int i = 0; i < keys.length; i++) {
                    String hexString = Integer.toHexString(keys[i]).toUpperCase();

                    Trie trieParent = currentTrie.get(hexString);
                    if (trieParent == null) {//如果没有此值,直接put进去一个空对象
                        currentTrie.put(hexString, new Trie());
                        trieParent = currentTrie.get(hexString);
                    }
                    Trie trie = trieParent.getNextTire();//获取此对象的下一个

                    if (keys.length - 1 == i) {//最后一个字了,需要把拼音写进去
                        trieParent.pinyin = value;
                        break;//此行其实并没有意义
                    }

                    if (trie == null) {
                        if (keys.length - 1 != i) {
                            //不是最后一个字,写入这个字的nextTrie,并匹配下一个
                            Trie subTrie = new Trie();
                            trieParent.setNextTire(subTrie);
                            subTrie.put(Integer.toHexString(keys[i + 1]).toUpperCase(), new Trie());
                            currentTrie = subTrie;
                        }
                    } else {
                        currentTrie = trie;
                    }

                }
            }
        } finally {
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (bufferedReader != null)
                bufferedReader.close();
        }
    }

    /**
     * 加载用户自定义的扩展词库
     */
    public void loadMultiPinyinExtend() throws IOException {
        String path = MultiPinyinConfig.multiPinyinPath;
        if (path != null) {
            File userMultiPinyinFile = new File(path);
            if (userMultiPinyinFile.exists()) {
                loadMultiPinyin(new FileInputStream(userMultiPinyinFile));
            } else {
                InputStream resourceAsStream = getClass().getResourceAsStream(path);
                if (resourceAsStream != null) {
                    loadMultiPinyin(new BufferedInputStream(resourceAsStream));
                }
            }
        }
    }

    public Trie get(String hexString) {
        return values.get(hexString);
    }

    public void put(String s, Trie trie) {
        values.put(s, trie);
    }
}
