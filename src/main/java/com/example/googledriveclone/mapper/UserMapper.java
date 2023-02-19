package com.example.googledriveclone.mapper;

import com.example.googledriveclone.dto.UserDto;
import com.example.googledriveclone.models.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper() {
       var mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        this.modelMapper = mapper;
    }

    public User UserDtoToUser(UserDto user){
        return modelMapper.map(user, User.class);
    }

}
