package com.erp.zup.api.dto.user.response;

import com.erp.zup.api.dto.BaseDTO;
import com.erp.zup.api.dto.user.request.RoleRequestDTO;
import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDTO extends BaseDTO {
    public String email;
    public String name;
    public List<RoleRequestDTO> roles;
}
