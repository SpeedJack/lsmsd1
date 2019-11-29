@OneToOne(fetch=FetchType.EAGER) //(1)
@JoinColumn(name="ownerId", nullable=false) //(2)
protected User_ owner;

/* (1) @OneToOne specifies that this is a one-to-one
 * 	relationship. fetch=FetchType.EAGER means that this
 * 	field should be initialized when the parent
 * 	(Restaurant_) object in created.
 * (2) @JoinColumn specifies the ownerId field as the join
 * 	column for Restaurant_.
 */
