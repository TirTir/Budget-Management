package backend.budget.auth.repository;

import backend.budget.auth.entity.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<BlackList, String> {

}