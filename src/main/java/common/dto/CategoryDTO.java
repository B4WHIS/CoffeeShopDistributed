package common.dto;

import java.io.Serializable;

public record CategoryDTO(
			int id,
			String name
		) implements Serializable {
	
}
