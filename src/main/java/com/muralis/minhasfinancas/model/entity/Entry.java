package com.muralis.minhasfinancas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;

import javax.persistence.*;

@Entity
@Table(name = "entry")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Entry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "description")
	private String description;
	
	@Column(name = "month")
	private Integer month;
	
	@Column(name = "year")
	private Integer year;

	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	@Column(name = "value")
	private BigDecimal value;

	@Column(name = "registration_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate registrationDate;

	@JsonIgnore
	@Column(name = "updated_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate updatedDate;

	@Column(name = "type")
	@Enumerated(value = EnumType.STRING)
	private TypeEntry type;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private StatusEntry status;

	@ManyToMany (fetch = FetchType.EAGER)
	@JoinTable(
			name = "categories_entries",
			joinColumns = @JoinColumn(name = "entry_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private Collection<Category> category = new ArrayList<>();

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

}
