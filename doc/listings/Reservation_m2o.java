@ManyToOne(fetch=FetchType.EAGER)
@JoinColumn(name="userId", nullable=false)
@Attribute(name="userId", isEntity=true)
protected User_ user;

@ManyToOne(fetch=FetchType.EAGER)
@JoinColumn(name="restaurantId", nullable=false)
@Attribute(name="restaurantId", isEntity=true)
protected Restaurant_ restaurant;
