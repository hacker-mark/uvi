
require 'json'
require 'net/http'
require 'uri'

require 'drivers'

class DriverRouteController < ApplicationController
	protect_from_forgery with: :null_session
	
	@@stuff = 0

	def index

		start = params["start"]
		destination = params["destination"]
		rider_start = params["rider_start"]
		rider_destination = params["rider_destination"]

		url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=key" %[start, destination]
		result = http_get(url)
		driver_time = total_time(result)
		
		url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&waypoints=%s|%s&key=key" %[start, destination, rider_start, rider_destination]
		result = http_get(url)
		rider_time = total_time(result)
		
		@@stuff = @@stuff + 1
		
		render :json => {"time" => driver_time, "stuff" => @@stuff, "time2" => rider_time}
	end
	
	def total_time(google_directions)
		route = google_directions["routes"][0]
		legs = route["legs"]
		time = 0
		for leg in legs
			time = time + leg["duration"]["value"]
		end
		return time
	end
	
	def http_get(url)
		url = URI.encode(url)
		url = URI.parse(url)
		req = Net::HTTP::Get.new(url.to_s)
		http = Net::HTTP.new(url.host, url.port)
		http.use_ssl = (url.scheme == "https")
		res = http.request(req)
		
		result = JSON.parse(res.body)
		return result
	end

end

