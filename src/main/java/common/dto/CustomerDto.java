package common.dto;

import java.io.Serializable;

public record CustomerDto(
			int id,
			String phoneNumber,
			String fullName,
			int accumulatedPoints,
			String memberRank,
			String createdAt
		) implements Serializable {
	
}
