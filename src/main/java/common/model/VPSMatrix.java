package common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPSMatrix {
    String id;
    int from;
    int to;

    public VPSMatrix(Object id, Object from, Object to) {
        this.id = ((String) id).trim();
        this.from = Integer.parseInt((String) from);
        this.to = Integer.parseInt((String) to);
    }
}
