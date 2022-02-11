package com.erp.zup.api.dto.user.response;

import com.erp.zup.api.dto.BaseDTO;
import com.erp.zup.api.dto.user.request.RoleRequestDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class UserResponseDTO extends BaseDTO {
    public String email;
    public String name;
    public List<RoleRequestDTO> roles;
}
