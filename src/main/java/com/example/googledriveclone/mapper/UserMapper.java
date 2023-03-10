package com.example.googledriveclone.mapper;

import com.example.googledriveclone.dto.UserDto;
import com.example.googledriveclone.models.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Destination;
import javax.xml.transform.Source;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper() {
       this.modelMapper = new ModelMapper();

        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
    }

    public User toEntity(UserDto userDto){
        return modelMapper.map(userDto, User.class);
    }

    public UserDto toDto(User user){
        return  modelMapper.map(user, UserDto.class);
    }

}
