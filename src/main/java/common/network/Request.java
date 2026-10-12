	package common.network;

import java.io.Serializable;

public record Request(
			String action,
			Object data
		)implements Serializable {

}
