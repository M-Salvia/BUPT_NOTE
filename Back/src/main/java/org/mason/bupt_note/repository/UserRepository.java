package org.mason.bupt_note.repository;

import org.mason.bupt_note.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByStudentId(String studentId);

    User findByGithubUsername(String githubUsername);
}