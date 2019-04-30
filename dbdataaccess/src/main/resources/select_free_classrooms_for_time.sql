select c.*
from fcimapp.classrooms c
left join fcimapp.calendar ca1
    on ca1.date_key = ?
left join fcimapp.schedule s
   on c.classroom_id = s.classroom_id
     and s.week_day = ca1.week_day_number
     and s.class_number = ?
     and (s.week_parity is null or s.week_parity = ca1.week_parity)
where s.entry_id is null and c.availability = 1
order by condition_rate desc, type asc
