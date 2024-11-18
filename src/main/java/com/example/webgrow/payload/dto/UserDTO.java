package com.example.webgrow.payload.dto;


import com.example.webgrow.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;

    public static UserDTO from(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstname(user.getFirstName());
        userDTO.setLastname(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setMobile(user.getMobile());
        return userDTO;
    }
}
