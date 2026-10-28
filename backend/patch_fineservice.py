with open("src/main/java/com/library/service/FineService.java", "r") as f:
    content = f.read()

content = content.replace("return fineDao.findById(id);", "return fineDao.findById(id).orElse(null);")
content = content.replace("return fineDao.findByTransactionId(transactionId);", "return fineDao.findByTransactionId(transactionId).orElse(null);")
content = content.replace("Fine fine = fineDao.findById(fineId);", "Fine fine = fineDao.findById(fineId).orElseThrow(() -> new RuntimeException(\"Fine not found\"));")

with open("src/main/java/com/library/service/FineService.java", "w") as f:
    f.write(content)
