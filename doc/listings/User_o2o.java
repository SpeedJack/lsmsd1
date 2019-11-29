@OneToOne(mappedBy="owner", fetch=FetchType.LAZY) //(1)
@LazyCollection(LazyCollectionOption.EXTRA)
protected Restaurant_ restaurant;

/* (1) mappedBy defines the name of the relation field in
 * 	the opposite entity. fetch=FetchType.LAZY specifies
 * 	that this field should be initialized only when it is
 * 	accessed by its getter and not when the object is
 * 	created.
 */
