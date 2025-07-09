package com.seidbros.movieinsight.service;




import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseService {


    Base<?> success(Object ob){
        return new Base<>(ob, true,"Success!");
    }

    Base<?> error(Object ob){
        return new Base<>(ob, false,"Error");
    }

    Base<?> error(){
        return new Base<>(null, false,"Unknown Error Occurred!");
    }

    public Base<?> error(String message){
        return new Base<>(null, false,message);
    }

    BaseList<?> success(List<?> obs){
        return new BaseList<>(obs, true, obs.size(), 0,"Success!");
    }BaseList<?> success(List<?> obs, long size, long pages){
        return new BaseList<>(obs, true, size,pages, "Success!");
    }

    BaseList<?> listError(){
        return new BaseList<>(null, false,0,0, "Requested list not found!");
    }
    BaseList<?> listError(String message){
        return new BaseList<>(null, false,0,0,message);
    }

    public ResponseEntity<?> rest(Base<?> base){
        return new ResponseEntity<>(base ,  base.isStatus() ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    public ResponseEntity<?> restOb(Base<?> base){
        return new ResponseEntity<>(base ,  base.isStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

   public ResponseEntity<?> restList(BaseList<?> base){
        return new ResponseEntity<>(base ,  base.isStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


}
