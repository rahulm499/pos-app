package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Users",
		indexes = { @Index(name = "email", columnList = "email"),
				@Index(name = "role", columnList = "role"),
				@Index(name = "email_pass", columnList = "email, password")
		}

)
public class UserPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique=true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String role;


}
