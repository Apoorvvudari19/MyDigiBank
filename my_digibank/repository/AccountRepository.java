package com.mydigibank.my_digibank.repository;

import com.mydigibank.my_digibank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByStatus(String status);
    List<Account> findByAccountType(String accountType);
    List<Account> findByAccountTypeAndStatus(String accountType, String status);
    
    List<Account> findByAccountHolderNameAndEmailAndPhoneNumberAndAccountType(
    	    String accountHolderName,
    	    String email,
    	    String phoneNumber,
    	    String accountType
    	);
}
