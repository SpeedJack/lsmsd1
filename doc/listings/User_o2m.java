@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
@LazyCollection(LazyCollectionOption.EXTRA)
@Where(clause="date >= CURRENT_DATE()")
protected List<Reservation_> activeReservations = new ArrayList<>();
