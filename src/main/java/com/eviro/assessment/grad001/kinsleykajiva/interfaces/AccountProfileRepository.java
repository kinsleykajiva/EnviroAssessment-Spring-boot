package com.eviro.assessment.grad001.kinsleykajiva.interfaces;

import com.eviro.assessment.grad001.kinsleykajiva.models.AccountProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface  AccountProfileRepository extends CrudRepository<AccountProfile, Long> {


    AccountProfile findByNameAndSurname(String name, String surname);



}
