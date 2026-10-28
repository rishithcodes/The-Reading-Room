with open("src/main/java/com/library/service/BookService.java", "r") as f:
    content = f.read()

content = content.replace("Boolean availability", "String availability")
content = content.replace("return bookDao.findById(id);", "return bookDao.findById(id).orElseThrow(() -> new RuntimeException(\"Book not found\"));")

with open("src/main/java/com/library/service/BookService.java", "w") as f:
    f.write(content)
