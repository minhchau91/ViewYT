package common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Email {
    private int id;
    private String username;
    private String password;
    private String recoveryEmail;
    private Proxy proxy;

    public Email(int id, String username, String password, String recoveryEmail) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.recoveryEmail = recoveryEmail;
    }
}
