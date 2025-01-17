/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.hr.db.repo;

import com.axelor.apps.hr.db.Timesheet;
import com.axelor.apps.hr.db.TimesheetLine;
import com.axelor.apps.hr.service.timesheet.TimesheetCreateService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineComputeNameService;
import com.axelor.apps.hr.service.timesheet.TimesheetPeriodComputationService;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Map;

public class TimesheetHRRepository extends TimesheetRepository {

  protected TimesheetLineComputeNameService timesheetLineComputeNameService;
  protected TimesheetPeriodComputationService timesheetPeriodComputationService;

  @Inject
  public TimesheetHRRepository(
      TimesheetLineComputeNameService timesheetLineComputeNameService,
      TimesheetPeriodComputationService timesheetPeriodComputationService) {
    this.timesheetLineComputeNameService = timesheetLineComputeNameService;
    this.timesheetPeriodComputationService = timesheetPeriodComputationService;
  }

  @Override
  public Timesheet save(Timesheet timesheet) {
    if (timesheet.getTimesheetLineList() != null) {
      for (TimesheetLine timesheetLine : timesheet.getTimesheetLineList()) {
        timesheetLineComputeNameService.computeFullName(timesheetLine);
      }
    }

    timesheet.setPeriodTotal(timesheetPeriodComputationService.computePeriodTotal(timesheet));
    return super.save(timesheet);
  }

  @Override
  public Map<String, Object> validate(Map<String, Object> json, Map<String, Object> context) {

    Map<String, Object> obj = super.validate(json, context);

    if (json.get("id") == null) {
      Timesheet timesheet = create(json);
      if (timesheet.getTimesheetLineList() == null || timesheet.getTimesheetLineList().isEmpty()) {
        TimesheetCreateService timesheetCreateService = Beans.get(TimesheetCreateService.class);
        timesheet.setTimesheetLineList(new ArrayList<>());
        obj.put("timesheetLineList", timesheetCreateService.createDefaultLines(timesheet));
      }
    }

    return obj;
  }
}
