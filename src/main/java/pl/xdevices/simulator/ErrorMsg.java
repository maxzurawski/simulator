package pl.xdevices.simulator;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorMsg {
    private int status;
    private String msg;
    private String details;
}
