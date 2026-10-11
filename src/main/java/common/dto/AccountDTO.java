package common.dto;

import java.io.Serializable;

public record AccountDTO(
		int id,
		String username,
		String fullName,
		String role,
		String phoneNumber,
		boolean active
			) implements Serializable {
}
