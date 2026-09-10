package common.protocol;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String action;
	private Object data;

}
