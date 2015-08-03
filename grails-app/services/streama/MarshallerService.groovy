package streama


import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

@Transactional
class MarshallerService {

    def springSecurityService
    def settingsService

    def init() {

        JSON.registerObjectMarshaller(User) {  User user ->
            def returnArray = [:]

            returnArray['id'] = user.id
            returnArray['username'] = user.username
            returnArray['authorities'] = user.authorities
            returnArray['enabled'] = user.enabled
            returnArray['dateCreated'] = user.dateCreated
            returnArray['fullName'] = user.fullName
            returnArray['invitationSent'] = user.invitationSent
            returnArray['favoriteGenres'] = user.favoriteGenres
            returnArray['isAdmin'] = (user.authorities.find{it.authority == 'ROLE_ADMIN'} ? true : false)
            returnArray['isContentManager'] = (user.authorities.find{it.authority == 'ROLE_CONTENT_MANAGER'} ? true : false)

            if(user.invitationSent && user.uuid){
              returnArray['invitationLink'] = settingsService.baseUrl +  "/invite?uuid=${user?.uuid}"
            }

            return returnArray;
        }

        JSON.registerObjectMarshaller(File) {  File file ->
            def returnArray = [:]

            returnArray['id'] = file.id
            returnArray['name'] = file.name
            returnArray['sha256Hex'] = file.sha256Hex
            returnArray['src'] = file.getSrc()
            returnArray['originalFilename'] = file.originalFilename
            returnArray['extension'] = file.extension
            returnArray['contentType'] = file.contentType
            returnArray['size'] = file.size
            returnArray['dateCreated'] = file.dateCreated
            returnArray['quality'] = file.quality

            return returnArray;
        }

        JSON.registerObjectMarshaller(Movie){ Movie movie ->
            def returnArray = [:]

            returnArray['id'] = movie.id
            returnArray['dateCreated'] = movie.dateCreated
            returnArray['lastUpdated'] = movie.lastUpdated
            returnArray['poster_path'] = movie.poster_path
            returnArray['release_date'] = movie.release_date
            returnArray['title'] = movie.title
            returnArray['overview'] = movie.overview
            returnArray['apiId'] = movie.apiId
            returnArray['original_language'] = movie.original_language
            returnArray['vote_average'] = movie.vote_average
            returnArray['vote_count'] = movie.vote_count
            returnArray['popularity'] = movie.popularity
            returnArray['files'] = movie.files

//            returnArray['viewedStatus'] = ViewingStatus.findByVideoAndUser(movie, springSecurityService.currentUser)

            return returnArray;
        }

        JSON.registerObjectMarshaller(Video) {  Video video ->
            def returnArray = [:]

            returnArray['id'] = video.id
            returnArray['dateCreated'] = video.dateCreated
            returnArray['lastUpdated'] = video.lastUpdated
            returnArray['overview'] = video.overview
            returnArray['imdb_id'] = video.imdb_id
            returnArray['vote_average'] = video.vote_average
            returnArray['vote_count'] = video.vote_count
            returnArray['popularity'] = video.popularity
            returnArray['original_language'] = video.original_language
            returnArray['apiId'] = video.apiId

            returnArray['files'] = video.files.findAll{it.extension != '.srt'}
            returnArray['subtitles'] = video.files.findAll{it.extension == '.srt'}

            returnArray['viewedStatus'] = ViewingStatus.findByVideoAndUser(video, springSecurityService.currentUser)

            if(video instanceof Episode){
                returnArray['show'] = video.show
                returnArray['episodeString'] = video.episodeString
                returnArray['name'] = video.name
                returnArray['air_date'] = video.air_date
                returnArray['season_number'] = video.season_number
                returnArray['episode_number'] = video.episode_number
                returnArray['still_path'] = video.still_path

                Video nextEpisode

                nextEpisode = video.show.episodes?.find{
                    return (it.episode_number == video.episode_number+1 && it.season_number == video.season_number)
                }
                if(!nextEpisode){
                    video.show.episodes?.find{
                        return (it.season_number == video.season_number+1 && it.episode_number == 1)
                    }
                }

                if(nextEpisode && nextEpisode.files){
                    returnArray['nextEpisode'] = nextEpisode
                }
            }
            if(video instanceof Movie){
                returnArray['title'] = video.title
                returnArray['release_date'] = video.release_date
                returnArray['backdrop_path'] = video.backdrop_path
                returnArray['poster_path'] = video.poster_path
            }

            return returnArray;
        }


        JSON.createNamedConfig('fullShow') { DefaultConverterConfiguration<JSON> cfg ->
            cfg.registerObjectMarshaller(TvShow) { TvShow  tvShow ->
                def returnArray = [:]

                returnArray['id'] = tvShow.id
                returnArray['dateCreated'] = tvShow.dateCreated
                returnArray['lastUpdated'] = tvShow.lastUpdated
                returnArray['name'] = tvShow.name
                returnArray['overview'] = tvShow.overview
                returnArray['apiId'] = tvShow.apiId
                returnArray['backdrop_path'] = tvShow.backdrop_path
                returnArray['poster_path'] = tvShow.poster_path
                returnArray['first_air_date'] = tvShow.first_air_date
                returnArray['original_language'] = tvShow.original_language
                returnArray['vote_average'] = tvShow.vote_average
                returnArray['vote_count'] = tvShow.vote_count
                returnArray['imdb_id'] = tvShow.imdb_id
                returnArray['popularity'] = tvShow.popularity
                returnArray['episodesWithFilesCount'] = tvShow.episodes.findAll{it.files}.size()
                returnArray['episodesCount'] = tvShow.episodes.size()

                return returnArray;
            }
        }


        JSON.createNamedConfig('fullViewingStatus') { DefaultConverterConfiguration<JSON> cfg ->
            cfg.registerObjectMarshaller(ViewingStatus) { ViewingStatus  viewingStatus ->
                def returnArray = [:]

                returnArray['id'] = viewingStatus.id
                returnArray['dateCreated'] = viewingStatus.dateCreated
                returnArray['lastUpdated'] = viewingStatus.lastUpdated
                returnArray['video'] = viewingStatus.video
                returnArray['tvShow'] = viewingStatus.tvShow
                returnArray['user'] = viewingStatus.user
                returnArray['currentPlayTime'] = viewingStatus.currentPlayTime
                returnArray['runtime'] = viewingStatus.runtime

                return returnArray;
            }
        }


        JSON.createNamedConfig('fullMovie') { DefaultConverterConfiguration<JSON> cfg ->
            cfg.registerObjectMarshaller(Movie) { Movie  movie ->
                def returnArray = [:]

              returnArray['id'] = movie.id
              returnArray['dateCreated'] = movie.dateCreated
              returnArray['lastUpdated'] = movie.lastUpdated
              returnArray['overview'] = movie.overview
              returnArray['imdb_id'] = movie.imdb_id
              returnArray['vote_average'] = movie.vote_average
              returnArray['vote_count'] = movie.vote_count
              returnArray['popularity'] = movie.popularity
              returnArray['original_language'] = movie.original_language
              returnArray['apiId'] = movie.apiId

              returnArray['title'] = movie.title
              returnArray['release_date'] = movie.release_date
              returnArray['backdrop_path'] = movie.backdrop_path
              returnArray['poster_path'] = movie.poster_path


              returnArray['files'] = movie.files.findAll{it.extension != '.srt'}
              returnArray['subtitles'] = movie.files.findAll{it.extension == '.srt'}
              returnArray['similarMovies'] = movie.similarMovies

                return returnArray;
            }
        }
    }
}
