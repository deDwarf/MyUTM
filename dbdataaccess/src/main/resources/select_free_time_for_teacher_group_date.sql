select c.*
from fcimapp.classes_time_schedule c
left join (
     select s.entry_id, s.class_number
     from fcimapp.schedule s
     inner join fcimapp.calendar ca1
          on ca1.week_day_number = s.week_day
          and ca1.date_key = ?
          and (s.week_parity is null or s.week_parity = ca1.week_parity)
     where (s.group_id = ? or s.main_teacher_id = ?)
) qq
     on qq.class_number = c.class_number
where qq.entry_id is null
