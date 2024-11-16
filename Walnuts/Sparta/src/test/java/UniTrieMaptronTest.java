import com.pinecone.framework.unit.trie.GenericReparseNode;
import com.pinecone.framework.unit.trie.UniTrieMaptron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import java.util.AbstractMap;
public class UniTrieMaptronTest {

    private UniTrieMaptron<String, String> trieMap;

    @BeforeEach
    public void setUp() {
        trieMap = new UniTrieMaptron<>();
    }

    @Test
    public void testPutAndGet() {
        trieMap.put("a/b/c", "value1");
        trieMap.put("a/b/d", "value2");
        assertEquals("value1", trieMap.get("a/b/c"));
        assertEquals("value2", trieMap.get("a/b/d"));
        //assertNull(trieMap.get("a/b"));
    }

    @Test
    public void testPutReference() {
        trieMap.put("a/b/c", "value1");
        trieMap.putReference("ref1", new GenericReparseNode<>("a/b/c",trieMap));


        assertEquals("value1", trieMap.get("ref1"));
    }

    @Test
    public void testContainsKey() {
        trieMap.put("a/b/c", "value1");
        assertTrue(trieMap.containsKey("a/b/c"));
        assertFalse(trieMap.containsKey("a/b"));
    }

    @Test
    public void testContainsValue() {
        trieMap.put("a/b/c", "value1");
        assertTrue(trieMap.containsValue("value1"));
        assertFalse(trieMap.containsValue("value2"));
    }

    @Test
    public void testRemove() {
        trieMap.put("a/b/c", "value1");
        assertEquals("value1", trieMap.remove("a/b/c"));
        assertNull(trieMap.get("a/b/c"));
        assertNull(trieMap.remove("a/b/c")); // Key already removed
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = Map.of(
                "a/b/c", "value1",
                "x/y/z", "value2"
        );
        trieMap.putAll(map);

        assertEquals("value1", trieMap.get("a/b/c"));
        assertEquals("value2", trieMap.get("x/y/z"));
    }

    @Test
    public void testClear() {
        trieMap.put("a/b/c", "value1");
        trieMap.put("x/y/z", "value2");
        trieMap.clear();

        assertTrue(trieMap.isEmpty());
        assertNull(trieMap.get("a/b/c"));
        assertNull(trieMap.get("x/y/z"));
    }

    @Test
    public void testKeySet() {
        trieMap.put("a/b/c", "value1");
        trieMap.put("x/y/z", "value2");
        Set<String> keys = trieMap.keySet();

        assertTrue(keys.contains("a/b/c"));
        assertTrue(keys.contains("x/y/z"));
    }

    @Test
    public void testValues() {
        trieMap.put("a/b/c", "value1");
        trieMap.put("x/y/z", "value2");
        assertTrue(trieMap.values().contains("value1"));
        assertTrue(trieMap.values().contains("value2"));
    }


    @Test
    public void testEntrySet() {
        // 初始化测试数据
        trieMap.put("apple", "fruit");
        trieMap.put("banana", "fruit");
        trieMap.put("car", "vehicle");
        trieMap.put("cat", "animal");

        // 获取 entrySet
        Set<Map.Entry<String, String>> entrySet = trieMap.entrySet();

        // 确保 entrySet 的大小与 Trie 中的键值对数量一致
        assertEquals(4, entrySet.size(), "EntrySet should contain 4 entries.");

        // 检查具体的键值对是否正确
        assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("apple", "fruit")), "EntrySet should contain ('apple', 'fruit').");
        assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("banana", "fruit")), "EntrySet should contain ('banana', 'fruit').");
        assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("car", "vehicle")), "EntrySet should contain ('car', 'vehicle').");
        assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("cat", "animal")), "EntrySet should contain ('cat', 'animal').");

        // 移除一个键值对，确保 entrySet 反映了变化
        trieMap.remove("cat");
        entrySet = trieMap.entrySet();
        assertEquals(3, entrySet.size(), "EntrySet should contain 3 entries after removal.");
        assertFalse(entrySet.contains(new AbstractMap.SimpleEntry<>("cat", "animal")), "EntrySet should not contain ('cat', 'animal') after removal.");
    }

    @Test
    public void testEntrySetIsEmpty() {
        // 确保在空的 Trie 上 entrySet 为空
        Set<Map.Entry<String, String>> entrySet = trieMap.entrySet();
        assertTrue(entrySet.isEmpty(), "EntrySet should be empty for a new Trie.");
    }
}
