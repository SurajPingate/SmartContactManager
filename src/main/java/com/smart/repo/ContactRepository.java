package com.smart.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.smart.entities.Contact;

@Component
public interface ContactRepository extends JpaRepository<Contact, Integer>{

	@Query("from Contact as c where c.user.id = :userId")
	public List<Contact> findContactsByUserId(@Param("userId") int userId);
}
