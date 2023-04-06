package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Users",
		indexes = { @Index(name = "role", columnList = "role"),
				@Index(name = "email_pass", columnList = "email, password")
		}

)
public class UserPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
	@TableGenerator(name = "user_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "user_id", initialValue = 1000, allocationSize = 1)
	private Integer id;
	@Column(unique=true, nullable = false) // UNIQUE KEY CHCEK KR LENA
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String role;


}
