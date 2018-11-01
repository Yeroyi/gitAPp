package com.jbufa.firefighting.model;

public class UserTokenBean {
    private UserToken jwtAuthenticationDto;


    public UserToken getJwtAuthenticationDto() {
        return jwtAuthenticationDto;
    }

    public void setJwtAuthenticationDto(UserToken jwtAuthenticationDto) {
        this.jwtAuthenticationDto = jwtAuthenticationDto;
    }

    public class UserToken {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
