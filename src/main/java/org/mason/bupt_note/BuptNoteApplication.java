package org.mason.bupt_note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BuptNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuptNoteApplication.class, args);
    }

}
