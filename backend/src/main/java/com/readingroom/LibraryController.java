package com.readingroom;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LibraryController {
 private final LibraryService service; private final UserRepository users; private final SessionService sessions; private final FineRepository fines;
 public LibraryController(LibraryService s,UserRepository u,SessionService ss,FineRepository f){service=s;users=u;sessions=ss;fines=f;}
 private User who(String auth){return service.authenticated(auth);}
 private Map<String,Object> response(Object data,String message){return Map.of("success",true,"message",message,"data",data==null?Map.of():data);}

 @GetMapping("/books") public Map<String,Object> books(@RequestHeader(value="Authorization",required=false)String a,@RequestParam Map<String,String> q){who(a);return service.bookList(q);}
 @GetMapping("/books/{id}") public Map<String,Object> book(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){who(a);return service.oneBook(id);}
 @PostMapping("/books") public Map<String,Object> createBook(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> body){service.allow(who(a),"ADMIN","LIBRARIAN");return service.saveBook(null,body);}
 @PutMapping("/books/{id}") public Map<String,Object> updateBook(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id,@RequestBody Map<String,Object> body){service.allow(who(a),"ADMIN","LIBRARIAN");return service.saveBook(id,body);}
 @DeleteMapping("/books/{id}") public Map<String,Object> deleteBook(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.allow(who(a),"ADMIN");service.deleteBook(id);return response(Map.of("id",id),"Book removed");}
 @GetMapping("/categories") public Map<String,Object> categories(@RequestHeader(value="Authorization",required=false)String a){who(a);return response(service.categoryList(),"Categories loaded");}
 @GetMapping("/authors") public Map<String,Object> authors(@RequestHeader(value="Authorization",required=false)String a){who(a);return response(service.authorList(),"Authors loaded");}
 @GetMapping("/publishers") public Map<String,Object> publishers(@RequestHeader(value="Authorization",required=false)String a){who(a);return response(service.publisherList(),"Publishers loaded");}
 @PostMapping("/categories") public Map<String,Object> createCategory(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.saveCategory(null,b);}
 @PutMapping("/categories/{id}") public Map<String,Object> updateCategory(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.saveCategory(id,b);}
 @DeleteMapping("/categories/{id}") public Map<String,Object> deleteCategory(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.allow(who(a),"ADMIN");service.deleteCategory(id);return response(Map.of("id",id),"Category deleted");}
 @PostMapping("/authors") public Map<String,Object> createAuthor(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.saveAuthor(null,b);}
 @PutMapping("/authors/{id}") public Map<String,Object> updateAuthor(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.saveAuthor(id,b);}
 @DeleteMapping("/authors/{id}") public Map<String,Object> deleteAuthor(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.allow(who(a),"ADMIN");service.deleteAuthor(id);return response(Map.of("id",id),"Author deleted");}
 @PostMapping("/publishers") public Map<String,Object> createPublisher(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.savePublisher(null,b);}
 @PutMapping("/publishers/{id}") public Map<String,Object> updatePublisher(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id,@RequestBody Map<String,Object> b){service.allow(who(a),"ADMIN");return service.savePublisher(id,b);}
 @DeleteMapping("/publishers/{id}") public Map<String,Object> deletePublisher(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.allow(who(a),"ADMIN");service.deletePublisher(id);return response(Map.of("id",id),"Publisher deleted");}

 @GetMapping("/users") public Map<String,Object> userList(@RequestHeader(value="Authorization",required=false)String a,@RequestParam Map<String,String> q){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN");if(u.getRole().equals("LIBRARIAN"))q.put("role","STUDENT");return service.userList(q);}
 @PostMapping("/users") public Map<String,Object> createUser(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> body){service.allow(who(a),"ADMIN");return service.saveUser(null,body);}
 @PutMapping("/users/{id}") public Map<String,Object> updateUser(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id,@RequestBody Map<String,Object> body){service.allow(who(a),"ADMIN");return service.saveUser(id,body);}
 @DeleteMapping("/users/{id}") public Map<String,Object> deactivate(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.allow(who(a),"ADMIN");service.deactivate(id);return response(Map.of("id",id),"User deactivated");}

 @GetMapping("/transactions") public Map<String,Object> transactions(@RequestHeader(value="Authorization",required=false)String a,@RequestParam Map<String,String> q){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN","STUDENT");return service.transactionList(q,u);}
 @GetMapping("/transactions/my") public Map<String,Object> myTransactions(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"STUDENT");return service.userTransactions(u);}
 @PostMapping("/transactions/issue") public Map<String,Object> issue(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> body){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN");return service.issue(body,u);}
 @PostMapping("/transactions/return") public Map<String,Object> returnBook(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> body){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN");Object id=body.get("transactionId");if(id==null)id=body.get("id");return service.returnBook(id==null?0:Integer.parseInt(String.valueOf(id)),u);}

 @GetMapping("/reservations") public Map<String,Object> reservations(@RequestHeader(value="Authorization",required=false)String a){return service.reservationList(who(a));}
 @GetMapping("/reservations/my") public Map<String,Object> myReservations(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"STUDENT");return service.reservationList(u);}
 @PostMapping("/reservations") public Map<String,Object> reserve(@RequestHeader(value="Authorization",required=false)String a,@RequestBody Map<String,Object> body){User u=who(a);service.allow(u,"STUDENT");return service.reserve(Integer.parseInt(String.valueOf(body.get("bookId"))),u);}
 @DeleteMapping("/reservations/{id}") public Map<String,Object> cancelReservation(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){User u=who(a);service.cancelReservation(id,u);return response(Map.of("id",id),"Reservation cancelled");}

 @GetMapping("/fines") public Map<String,Object> fineList(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN","STUDENT");return response(Map.of("fines",service.fineList(u)),"Fines loaded");}
 @GetMapping("/fines/my") public Map<String,Object> myFines(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"STUDENT");return response(Map.of("fines",service.fineList(u)),"Fines loaded");}
 @PostMapping("/fines/{id}/pay") @Transactional public Map<String,Object> payFine(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){User u=who(a);service.allow(u,"ADMIN","LIBRARIAN");Fine f=fines.findById(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND,"Fine not found"));f.setPaidAmount(f.getAmount());f.setStatus("PAID");fines.save(f);return response(Map.of("id",id,"status","PAID"),"Fine payment recorded");}

 @GetMapping("/dashboard/admin") public Map<String,Object> adminDashboard(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"ADMIN","LIBRARIAN");return service.dashboardAdmin();}
 @GetMapping("/dashboard/librarian") public Map<String,Object> librarianDashboard(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"LIBRARIAN");return service.dashboardAdmin();}
 @GetMapping("/dashboard/student") public Map<String,Object> studentDashboard(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"STUDENT");return service.dashboardStudent(u);}

 @GetMapping("/recommendations/my") public Map<String,Object> recommendations(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"STUDENT");return response(Map.of("books",service.recommendationList(u)),"Recommendations loaded");}
 @GetMapping("/notifications/my") public Map<String,Object> notifications(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);return service.notificationList(u);}
 @GetMapping("/notifications/unread-count") public Map<String,Object> unreadCount(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);return response(Map.of("count",service.notificationList(u).get("data") instanceof Map<?,?> d?d.get("unreadCount"):0),"Unread count loaded");}
 @PutMapping("/notifications/{id}/read") public Map<String,Object> markRead(@RequestHeader(value="Authorization",required=false)String a,@PathVariable Integer id){service.markNotificationRead(id,who(a));return response(Map.of("id",id),"Notification marked read");}
 @PutMapping("/notifications/read-all") public Map<String,Object> markAllRead(@RequestHeader(value="Authorization",required=false)String a){service.markAllNotificationsRead(who(a));return response(Map.of(),"Notifications marked read");}

 @GetMapping("/settings") public Map<String,Object> settings(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"ADMIN");return service.settingsView();}
 @PutMapping("/settings/{key}") public Map<String,Object> updateSetting(@RequestHeader(value="Authorization",required=false)String a,@PathVariable String key,@RequestBody Map<String,String> body){service.allow(who(a),"ADMIN");return service.updateSetting(key,body.getOrDefault("value",""));}

 @GetMapping("/reports/transactions") public Map<String,Object> reportTransactions(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"ADMIN");return service.transactionList(Map.of(),u);}
 @GetMapping("/reports/issued") public Map<String,Object> reportIssued(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"ADMIN");return service.transactionList(Map.of("status","ISSUED"),u);}
 @GetMapping("/reports/overdue") public Map<String,Object> reportOverdue(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"ADMIN");return response(Map.of("transactions",service.overdueTransactions()),"Overdue report loaded");}
 @GetMapping("/reports/fines") public Map<String,Object> reportFines(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"ADMIN");return response(Map.of("fines",service.fineList(u)),"Fine report loaded");}
 @GetMapping("/reports/books") public Map<String,Object> reportBooks(@RequestHeader(value="Authorization",required=false)String a){User u=who(a);service.allow(u,"ADMIN");return service.bookList(Map.of("pageSize","100"));}
 @GetMapping("/reports/most-borrowed") public Map<String,Object> reportMostBorrowed(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"ADMIN");return response(Map.of("books",service.mostBorrowed()),"Popular books report loaded");}
 @GetMapping("/reports/users") public Map<String,Object> reportUsers(@RequestHeader(value="Authorization",required=false)String a){service.allow(who(a),"ADMIN");return service.userList(Map.of());}
}
