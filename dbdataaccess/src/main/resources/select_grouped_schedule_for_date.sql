select
	'regular' as class_type
	 , cast(
	JSON_ARRAYAGG(JSON_OBJECT('groupId', group_id, 'scheduleEntryId', schedule_entry_id, 'groupNumber', group_number)
		) as char(400)) as groupIdToEntryIdAndGroupNumber
	 , case when scc.entry_id is not null then 1 else 0 end as cancelled_flg
	 , c.date_key as `date`, sch.class_start_time, sch.class_end_time, sch.class_number
	 , classroom_name
	 , subgroup
	 , teacher_first_name, teacher_second_name, teacher_middle_name, teacher_full_name
	 , subject_type_abbreviated
	 , subject_name, subject_name_abbreviated
	 , subject_id, subject_type_id, teacher_id, classroom_id
from fcimapp.calendar c
inner join fcimapp.vw_denormalized_regular_schedule sch
	on c.week_day_number = sch.week_day
left join fcimapp.schedule_cancelled_classes scc
	on scc.schedule_id = sch.schedule_entry_id
	and scc.cancelled_for_date = c.date_key
where
	(sch.week_parity is null or c.week_parity = sch.week_parity)
	and (sch.group_id = ? or sch.teacher_id = ?)
	and c.date_key between ? and ?
group by subject_id, subject_type_id, teacher_id, classroom_id
			 , c.date_key, class_number, class_type
union all
select
	'dated' as class_type
		 , cast(
		JSON_ARRAYAGG(JSON_OBJECT('groupId', group_id, 'scheduleEntryId', schedule_entry_id, 'groupNumber', group_number)
			) as char(400)) as groupIdToEntryIdAndGroupNumber
		 , 0 as cancelled_flg
		 , date_key as `date`, schd.class_start_time, schd.class_end_time, schd.class_number
		 , classroom_name
		 , subgroup
		 , teacher_first_name, teacher_second_name, teacher_middle_name, teacher_full_name
		 , subject_type_abbreviated
		 , subject_name, subject_name_abbreviated
		 , subject_id, subject_type_id, teacher_id, classroom_id
from fcimapp.vw_denormalized_dated_schedule schd
where
	(schd.group_id = ? or schd.teacher_id = ?)
	and schd.date_key between ? and ?
group by subject_id, subject_type_id, teacher_id, classroom_id
			 , schd.date_key, class_number, class_type
