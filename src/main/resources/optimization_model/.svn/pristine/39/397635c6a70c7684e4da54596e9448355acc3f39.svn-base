#####################
###  SelfLet optimization model
###  Author: Nicola Calcavecchia
#####################

######################################################
######### PARAMETERS #########
######################################################

param N > 0 integer ; ### number of SelfLets
set Selflets := 1..N ;
 
param S > 0 integer ; ### number of services
set Services := 1..S ;

### Total request rate for service s
param Lambda {Services} >= 0 ;

### Maximum response time for service s
param Max_response_time {Services} >= 0 ;

param DR > 0;

param BIG_VALUE > 0 integer ;

param SMALL_VALUE := 1/BIG_VALUE ; 

param MIN_SELFLETS >= 0;

###################################################
######### SETS 	  #########
###################################################

set behavior_structure dimen 4 ;
set implementing_behaviors dimen 2;

# number of behaviors
param B := card(implementing_behaviors);

set Behaviors := 1..B;

set service_of_state  dimen 3 ;

set states_in_behavior {b in Behaviors} := setof{ (bb, ii, ss) in service_of_state: b = bb } (ii) ; 

param states_of_behavior {b in Behaviors} := card( setof{ (bb, ii, ss) in service_of_state: b = bb } (ii));
param max_states := max {b in Behaviors} states_of_behavior [b];

set States := 1..max_states;


### demands for states
set DL dimen 2 ;

param state_exists {b in Behaviors, i in States} := min(min(card{ (b1, t1, s1, p1) in behavior_structure: b1 = b and (s1 = i or t1 = i)}, 1), card{ (b1, t1, s1, p1) in behavior_structure: b1 = b and (s1 = i or t1 = i)}) ; 


param behavior_is_ability{b in Behaviors} := card{ (bb, ts, ss, p) in behavior_structure : bb = b and ts = 1 and ss = 1 } ; 

### probability for each state
param state_probability {b in Behaviors, i in States} = 
					if i = 1 
						then 1 
					else 
						sum{ (bb, ts, ss, p) in behavior_structure: bb = b and ts = i } 
						state_probability[b, ss] * p;


###################################################
######### VARIABLES #########
###################################################


### 1 if selflet n is active
var active_selflet 				{Selflets} binary;

### 1 if service is deployed on selflet
var service_on_selflet 			{Selflets, Services} binary; 

var behavior_in_selflet 		{Behaviors, Selflets} binary; 

### amount of DIRECT traffic directed to selflet n for service s
var lambda_services_selflets 	{Selflets, Services} >= 0 ;

var response_time_at_selflet 	{Selflets, Services} >= 0;

var response_time_of_state 		{Behaviors, States, Selflets} >= 0;
var state_locally_executed 		{Behaviors, States, Selflets} binary;
var state_remotely_executed 	{Behaviors, States, Selflets} binary;

var behavior_state_request_rate {Behaviors, Selflets, States} >= 0;
var state_demand 				{Behaviors, Selflets, States} >= 0;
var util 						{Behaviors, Selflets, States} in interval [0,1] ;

var	selflet_util 				{Selflets} in interval [0,1] ;

var selflet_util_inv 			{Selflets} >= 0 ; 

var lambda_redirect 			{Selflets, Behaviors, States, Selflets} >= 0 ;

var total_rate 					{Behaviors, Selflets} >= 0;

### request rate due to remote redirects
var remote_rate					{Behaviors, Selflets} >=0 ; 

### request rate due to local redirects
var local_rate					{Behaviors, Selflets} >= 0;


###################################################
######### OBJECTIVE #########
###################################################

minimize total_costs:
		sum {n in Selflets} active_selflet[n] ;

###################################################
######### CONSTRAINTS #########
###################################################

	
### Each service is placed in at least one selflet (i.e. place every service)
# PS. If active_selflet[n] is removed it will give unfeasible!
s.t. place_service {s in Services} :
		(sum {n in Selflets} service_on_selflet[n, s] * active_selflet[n] ) >= 1;

### A selflet is active if there is at least one service placed on it
s.t. no_service_if_selflet_is_passive {n in Selflets} :
		sum {s in Services} service_on_selflet[n, s] <= active_selflet[n] * S;

### If the service s is placed in selflet n, then exactly one behavior is implementing it
s.t. unique_behavior {n in Selflets, s in Services} :
		# select behaviors implementing service s (equality makes "exactly one")
		sum { (b, ss) in implementing_behaviors: ss = s }
			behavior_in_selflet[b, n] = service_on_selflet[n, s];

### Total rate for service s is spread over selflets
s.t. cumulative_request_rates {s in Services} :
		sum {n in Selflets} lambda_services_selflets[n, s] = Lambda[s] ;

### No request rate on selflet for service s if it is not deployed there
s.t. no_rate_if_no_service {n in Selflets, s in Services} :
		lambda_services_selflets[n, s] <= service_on_selflet[n, s] * Lambda[s] ;

### Fixes the request rate for the initial state of the behavior
s.t. behavior_initial_state {b in Behaviors, n in Selflets} :
		behavior_state_request_rate[b, n, 1] = total_rate[b, n] * behavior_in_selflet[b, n] * active_selflet[n];

### Defines request rates for other states
s.t. behavior_other_states {n in Selflets, b in Behaviors,  i in 2..states_of_behavior[b]} :
		behavior_state_request_rate[b, n, i] = behavior_in_selflet[b, n] * state_exists[b, i] *
		( sum{ (bb, ts, ss, probability) in behavior_structure: b = bb and ts = i} behavior_state_request_rate[bb, n, ss] * probability ) * active_selflet[n];

### define the maximum request rate for state i (is this used? )		
s.t. null_states {n in Selflets, b in Behaviors, i in States} :
		behavior_state_request_rate[b, n, i] <= behavior_in_selflet[b, n] * total_rate[b, n] * state_exists[b, i] * active_selflet[n];

### if the state does not exists, then both local and remote are zero
# THIS CONSTRAINT IS IMPLIED BY THE NEXT ONE...
#s.t. state_local_remote_non_executed {b in Behaviors, i in States, n in Selflets} :
#		state_locally_executed[b, i, n] + state_remotely_executed[b, i, n] <= state_exists[b, i];

### if behavior is is in the selflet and the state exists then either it is local or it is remote
s.t. state_executed_if_current_behavior {b in Behaviors, i in States, n in Selflets} :
		state_locally_executed[b, i, n] + state_remotely_executed[b, i, n] = behavior_in_selflet[b, n] * state_exists[b, i];
	
### if behavior is in selflet and it is ability, then it is locally executed
s.t. state_is_locally_executed_if_ability{b in Behaviors, i in States, n in Selflets} :
		state_locally_executed[b, i, n] >=  behavior_is_ability[b] * state_exists[b, i] * behavior_in_selflet[b, n];

### state is locally executed if the service requested by state i is available locally
s.t. state_is_locally_executed_if_service_on_selflet {b in Behaviors, i in States, n in Selflets} :
		state_locally_executed[b, i, n] >= (sum{ (bb, ii, ss) in service_of_state: bb = b and ii = i}  service_on_selflet[n, ss]) ;
		
		
# Is this constraint implied in the previous? 
s.t. not_available_services_are_remotely_executed {b in Behaviors, i in States, n in Selflets} :
		state_remotely_executed[b, i, n] >=  
				( sum{ (bb, ii, ss) in service_of_state: bb = b and ii = i}  (1 - service_on_selflet[n, ss])) * 
										( 1 - behavior_is_ability[b]) * state_exists[b, i] * behavior_in_selflet[b, n];

### define utilization for each single state of the selflet
s.t. state_utilization_1 {b in Behaviors, n in Selflets, i in States} : 
		util[b, n, i] = state_demand[b, n, i] * behavior_state_request_rate[b, n, i] * state_exists[b, i] ; 
		
### define utilization for each single state of the selflet
s.t. state_utilization_2 {b in Behaviors, n in Selflets, i in States} : 
		util[b, n, i] <= 1 - SMALL_VALUE ; 

# Define selflet utilization		
s.t. selflet_utilization {n in Selflets} :
		selflet_util[n] = sum {b in Behaviors, i in States} util[b, n, i];

# Limit selflet utilization 
s.t. utilization_threshold {n in Selflets} : 
		selflet_util[n] <= active_selflet[n];

### avoid saturation condition
s.t. utilization_threshold_2 {n in Selflets}:
	 	selflet_util[n] <= 1 - SMALL_VALUE;

s.t. service_redirect_lambda{n1 in Selflets, b in Behaviors, i in States}:
	sum {n2 in Selflets} lambda_redirect[n1, b, i, n2 ] = 
			behavior_state_request_rate[b, n1, i] * state_exists[b, i] * (1 - behavior_is_ability[b]);

### if the service required by state i is not present in selflet n2 then redirect rate is 0
s.t. lambda_redirect_constraint {n1 in Selflets, n2 in Selflets, b in Behaviors, i in States}:
	lambda_redirect[n1, b, i, n2 ] <= sum{(bb, ii, ss) in service_of_state: b = bb and i = ii} service_on_selflet[n2, ss] * BIG_VALUE ;

s.t. total_rate_constraint {b in Behaviors, n in Selflets}:
	total_rate[b,n]	= (sum{(bb, ss) in implementing_behaviors: bb = b } 
		lambda_services_selflets[n, ss]  + remote_rate[b,n] + local_rate[b,n]) * behavior_in_selflet[b, n];

s.t. remote_rate_constraint {b in Behaviors, n in Selflets}:
remote_rate[b,n] =
				sum{
			# find the service implemented by this behavior
				(this_behavior, this_behavior_service) in implementing_behaviors, 
				(sos_other_behavior, sos_other_state, sos_requested_services) in service_of_state, 
				other_selflet in Selflets : 
					this_behavior = b and 
					this_behavior_service = sos_requested_services and 
					other_selflet <> n} 
				lambda_redirect[other_selflet, sos_other_behavior, sos_other_state, n] * behavior_in_selflet[b, n] ;
	

s.t. local_redirects {b in Behaviors, n in Selflets} :
		local_rate[b, n] = behavior_in_selflet[b, n] * 
						sum{b_other in Behaviors, (bb, ss) in implementing_behaviors, (bb_this, ss_this) in implementing_behaviors, 
						(bb_2, ii, ss_2) in service_of_state: 
							b_other <> b and bb = b_other and bb_2 = b_other and ss_2 = ss_this and bb_this = b} 
		behavior_state_request_rate[b_other, n, ii] * 						
		state_locally_executed[b_other, ii, n] *  				
		state_exists[b_other, ii] *
		behavior_in_selflet[b_other, n]
		;
														
		
s.t. state_demand_constraint {b in Behaviors, n in Selflets, i in States} :
		state_demand[b, n, i] = state_exists[b, i] * sum{ (b_DL, dl_demand) in DL, (bb, ss) in implementing_behaviors: bb = b and b_DL == b} 
				# LOCAL EXECUTION
				( state_locally_executed[b, i, n] * behavior_in_selflet[b, n] * behavior_is_ability[b] * dl_demand + 
				# REMOTE EXECUTION
				state_remotely_executed[b, i, n] * DR ) ;


	
### Response time at each state
s.t. response_time_of_state_constraint {b in Behaviors, i in States, n in Selflets}:
	response_time_of_state[b, i, n] = 
					state_exists[b, i] * behavior_in_selflet[b, n] * 
					(
						# ABILITY
						state_locally_executed[b, i, n] * 
							( 
							#	behavior_is_ability[b] * 
								(state_demand[b, n, i] * selflet_util_inv[n])

						# LOCAL REDIRECT
								+ 
								(1 - behavior_is_ability[b]) * 
								( sum{ (bb, ii, ss) in service_of_state: bb = b and ii = i} 
									response_time_at_selflet[n, ss]
								)
							) 
							+ 
	#					# REMOTE EXECUTION
						state_remotely_executed[b, i, n]
						 * 
						 ( sum {m in Selflets, (bb, ii, ss) in service_of_state: bb = b and ii = i and m <> n} 
							lambda_redirect[n, b, i, m ] *  response_time_at_selflet[m, ss]) 
							/
							(max (SMALL_VALUE, sum{m in Selflets } lambda_redirect[n, b, i, m ]))
					);


s.t. inversion {n in Selflets}:
	selflet_util_inv[n] = 1 / (1 - selflet_util[n]);


s.t. response_time_at_selflet_constraint{n in Selflets, s in Services}:
	response_time_at_selflet[n, s] = 
		sum { (b, ss) in implementing_behaviors}
			sum {ii in states_in_behavior[b]: ss = s  } 
			( state_probability[b, ii] * response_time_of_state[b, ii, n] ) ;
		

### Response time for service s is bounded by a parameter OR use the next one
#s.t. maximum_response_time {s in Services: Lambda[s] > 0} :
#		( (sum{n in Selflets} lambda_services_selflets[n, s] * response_time_at_selflet[n , s]) / Lambda[s] )
#		<= Max_response_time[s];

### Response time is bounded by the maximum response time
s.t. maximum_response_time {n in Selflets, s in Services} :
		response_time_at_selflet[n , s] <= Max_response_time[s];

### FAKE CONSTRAINT															
#s.t. fake_constraint {n in Selflets} :
#		sum{s in Services} service_on_selflet[n, s] <= 2;

### At least this amount of seflet. This constraint is used to speed up the solver
s.t. fake_constraint_1:
	sum{n in Selflets} active_selflet[n] >= MIN_SELFLETS;
