package common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Proxy {
    private String host;
    private String port;
    private String username = "";
    private String password = "";

    public Proxy(String host, String port) {
        this.host = host;
        this.port = port;
    }
}
