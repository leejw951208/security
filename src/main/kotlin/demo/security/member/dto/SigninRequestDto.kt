package demo.security.member.dto

import jakarta.validation.constraints.NotBlank

data class SigninRequestDto(
    @field:NotBlank(message = "이메일은 필수 입니다.")
    val email: String?,

    @field:NotBlank(message = "비밀번호는 필수 입니다.")
    val password: String?
)