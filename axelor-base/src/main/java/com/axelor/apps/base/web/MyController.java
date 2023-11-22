package com.axelor.apps.base.web;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.message.db.Message;
import com.axelor.message.service.MessageService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class MyController {
  public void fulltest(ActionRequest request, ActionResponse response) {
    try {
      Message mes = request.getContext().asType(Message.class);
      String ter = Beans.get(MessageService.class).getFullEmailAddress(mes.getFromEmailAddress());
      String tr=ter;
      response.setValue("subject",tr);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
