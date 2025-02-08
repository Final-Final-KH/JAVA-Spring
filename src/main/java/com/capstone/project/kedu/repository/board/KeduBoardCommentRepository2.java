package com.capstone.project.kedu.repository.board;

import com.capstone.project.kedu.entity.board.KeduBoardCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeduBoardCommentRepository2  extends JpaRepository<KeduBoardCommentEntity2, Long> {

}
