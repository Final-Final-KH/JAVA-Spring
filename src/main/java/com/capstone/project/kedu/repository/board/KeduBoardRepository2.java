package com.capstone.project.kedu.repository.board;

import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeduBoardRepository2 extends JpaRepository<KeduBoardEntity2, Long> {
}
