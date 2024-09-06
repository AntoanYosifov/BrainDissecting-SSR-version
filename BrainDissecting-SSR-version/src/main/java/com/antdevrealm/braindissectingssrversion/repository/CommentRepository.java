package com.antdevrealm.braindissectingssrversion.repository;

import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

}
