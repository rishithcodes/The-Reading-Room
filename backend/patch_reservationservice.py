with open("src/main/java/com/library/service/ReservationService.java", "r") as f:
    content = f.read()

content = content.replace("reservationDao.findById(id).orElse(null)", "reservationDao.findById(id)")
content = content.replace("reservationDao.findById(reservationId).orElse(null)", "reservationDao.findById(reservationId)")

with open("src/main/java/com/library/service/ReservationService.java", "w") as f:
    f.write(content)
