
require 'json'
require 'drivers'

class TestController < ApplicationController
  before_filter :subscribe, :only => :index
  protect_from_forgery with: :null_session

  def index
	render :json => {}
  end

  def publish
    render :json => $pubnub.publish(
        :channel => 'driver_route',
        :callback => lambda {|x|},
        :message => {
            :start => params["start"],
            :destination => params["destination"],
            :user_id => "foo"
        }
    )
  end

  def publish_test
        puts "test"
        start = params["start"]
        destination = params["destination"]
        user_id = params["user_id"]
        Drivers.add({"start" => start, "destination" => destination, "user_id" => user_id})
            puts "added"
            puts Drivers.get()
     render :json => {}
  end

    def publish_rider
        render :json => $pubnub.publish(
        :channel => 'rider',
        :callback => lambda {|x|},
        :message => {
            :rider_start => params["rider_start"],
            :rider_destination => params["rider_destination"]
        }
    )
    end

  def get_messages
    render :json => Message.all
  end

  def status
    render :text => $pubnub.inspect
  end

  private

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

  def subscribe
    puts 'subscribing...'
    
    if not $pubnub.subscription_running?
		puts "really doing it"
	    $pubnub.subscribe(:channel => 'driver_route') do |envelope|
		    puts "received message driver_route:"
            puts envelope.message
            message = envelope.message
            puts message
            start = message["start"]
            destination = message["destination"]
            start_id = message["start_id"]
            destination_id = message["destination_id"]
            user_id = message["user_id"]
            phone = message["phone"]
            puts "adding"
            Drivers.add({"start" => start, "destination" => destination, "start_id" => start_id, "destination_id" => destination_id, "user_id" => user_id, "phone" => phone})
            puts "added"
            puts Drivers.get()
	    end
        $pubnub.subscribe(:channel => "rider") do |envelope|
            puts "received message rider"
            message = envelope.message
            drivers = Drivers.get()
            rider_start = message["rider_start"]
            rider_destination = message["rider_destination"]
            rider_id = message["user_id"]

            best = nil
            best_driver = {}

            for driver in drivers
                start = driver["start"]
		        destination = driver["destination"]

		        url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=key" %[start, destination]
		        result = http_get(url)
		        driver_time = total_time(result)
		
		        url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&waypoints=%s|%s&key=key" %[start, destination, rider_start, rider_destination]
		        result = http_get(url)
		        rider_time = total_time(result)

                diff = rider_time - driver_time
                if best.nil? or diff < best
                    best_driver = driver
                    best = diff
                end
            end

            $pubnub.publish(
                :channel => 'rider_response',
                :callback => lambda {|x|},
                :message => {
                    :driver => best_driver
                })
        end
        $pubnub.subscribe(:channel => "rider_response") do |envelope|
            puts "received message rider response"
            puts envelope.message
        end
    end
  end

end

