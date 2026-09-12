package common.dto;

import java.io.Serializable;

public record CategoryDto(
			int id,
			String name
		) implements Serializable {
	
}
