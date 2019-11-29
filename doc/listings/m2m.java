@ManyToMany
@JoinTable(name="table_name",
	joinColumns={@JoinColumn(name="column_name")},
	inverseJoinColumns={@JoinColumn(name="inverse_column_name")}
// field definition
