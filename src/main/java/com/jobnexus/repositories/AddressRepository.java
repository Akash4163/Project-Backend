package com.jobnexus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobnexus.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
