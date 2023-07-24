package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import study.datajpa.entity.Item;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    void saveItemTest() {
        Item item = new Item("AA");
        itemRepository.save(item);
    }
    
}
