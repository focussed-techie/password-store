package com.puneet.password.store.model;

import com.puneet.password.store.hash.HashCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@Component
public class PasswordConverter implements AttributeConverter<String,String> {



    private HashCreator hashCreator;





    @Override
    public String convertToDatabaseColumn(String attribute) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return hashCreator.encrypt(attribute,authentication.getCredentials().toString(),authentication.getName());


    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return hashCreator.decrypt(dbData,authentication.getCredentials().toString(),authentication.getName());
    }


}
