preferences {    
	section("Internal Access"){
		input "Broadlink RM+", "text", title: "ID & PW for RM Plugin or RM Bridge(Optional)", required: false
		input "192.168.0.110", "text", title: "IP for RM Plugin or RM Bridge or HA(Required for all)", required: true
		input "internal_port", "text", title: "Port(Required for all)", required: false
		input "internal_on_path", "text", title: "On Path(Required for all)", required: false
        input "internal_off_path", "text", title: "Off Path", required: false
	}
}

metadata {
	definition (name: "Broadlink: Double Momentary Button = Custom", namespace: "oniondigit51440", author: "btrial", "mnmn": "SmartThingsCommunity", "vid": "03bf1078-ad0f-3824-9533-b46428f086a9") {
		capability "Switch"
        capability 'oniondigit51440.momentaryButtonEnable'
        capability 'oniondigit51440.momentaryButtonDisable' 
		capability 'oniondigit51440.lastRunningDateTime' 
	}	
}

def push(String path) {

    if (path){
    	def userpass = "Basic " + id_pw.encodeAsBase64().toString()
		def result = new physicalgraph.device.HubAction(
				method: "Get",		/* If you want to use the RM Bridge, change the method from "POST" to "Get" */
				path: "${path}",
				headers: [HOST: "${internal_ip}:${internal_port}", AUTHORIZATION: "${userpass}"],
                body: ["entity_id":"${body_data_for_ha}"]
				)
			sendHubCommand(result)
			log.debug result
	}
}

def momentaryButtonEnable()
{
	setLastRunFor("Enable")
	log.debug( 'enabled called' )
	sendEvent( name: 'switch',  value: 'on' )
	sendEvent( name: 'switch',  value: 'undefined' )
    
    push(internal_on_path)
}

def momentaryButtonDisable()
{
	setLastRunFor("Disable")
	log.debug( 'disabled called' )
	sendEvent( name: 'switch',  value: 'off' )
	sendEvent( name: 'switch',  value: 'undefined' )
    
    push(internal_off_path)
}

def setLastRunFor(String value) 
{
	Date currentDate = new Date()
	def currentDateWithTimeZone = currentDate?.format('dd/MM/yyyy HH:mm:ss', location.timeZone)  
	setLastRunningDateTime(value + " - " + currentDateWithTimeZone)
    setMomentaryButtonEnableHeadline(value + " - " + currentDateWithTimeZone)
    setMomentaryButtonDisableHeadline(value + " - " + currentDateWithTimeZone)
}

def setLastRunningDateTime(String value) 
{
	sendEvent( name: 'lastRunningDateTime',  value: value )
}

def setMomentaryButtonEnableHeadline(String value) {
	sendEvent( name: 'momentaryButtonEnableHeadline',  value: value )
}

def setMomentaryButtonDisableHeadline(String value) {
	sendEvent( name: 'momentaryButtonDisableHeadline',  value: value )
}

def on() 
{
    log.debug( 'on called' )
    momentaryButtonEnable()
}

def off() 
{
    log.debug( 'off called' )
    momentaryButtonDisable()
}

def parse( String description )
{
    log.debug( 'parse called' )
}

def installed()
{	
	log.debug( 'installed')
    sendEvent( name: 'switch', value: 'undefined')
}

def updated()
{
	log.debug( 'updated called')
}