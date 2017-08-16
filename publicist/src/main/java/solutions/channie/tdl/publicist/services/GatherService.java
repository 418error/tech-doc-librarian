package solutions.channie.tdl.publicist.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Channie on 12/12/2016.
 */
@Service
public class GatherService {

    private static final Logger log = LoggerFactory.getLogger(GatherService.class);

    private static final String REMOTE_URL = "https://github.com/lendmeapound/tech-doc-sample.git";

    public void getRepos() throws IOException, GitAPIException {
        log.info("Starting");
        // prepare a new folder for the cloned repository
        File localPath = File.createTempFile("TestGitRepository", "");
        if(!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);

        // Clone a repo
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .setCloneAllBranches(true) //?????
                .call()) {
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            System.out.println("Having repository: " + result.getRepository().getDirectory());

            try (Repository repository = result.getRepository()) {

                //List all the branches of a cloned repo
                System.out.println("Listing local branches:");
                try (Git git = new Git(repository)) {
                    List<Ref> call = git.branchList().call();
                    for (Ref ref : call) {
                        //System.out.println("Branch: " + ref + " --- " + ref.getName() + " --- " + ref.getObjectId().getName());
                        System.out.println("Branch: "+ref.getName());
                    }

                    System.out.println("Now including remote branches:");
                    call = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
                    for (Ref ref : call) {
                        //System.out.println("Branch: " + ref + " --- " + ref.getName() + " --- " + ref.getObjectId().getName());
                        System.out.println("Branch: "+ref.getName());
                    }
                }

                //List all the tags of the cloned repo
                try (Git git = new Git(repository)) {
                    List<Ref> call = git.tagList().call();
                    for (Ref ref : call) {
                        //System.out.println("Tag: " + ref + " --- " + ref.getName() + " --- " + ref.getObjectId().getName());
                        System.out.println("Tag: "+ref.getName());

                        // fetch all commits for this tag
                        LogCommand log = git.log();

                        Ref peeledRef = repository.peel(ref);
                        if (peeledRef.getPeeledObjectId() != null) {
                            log.add(peeledRef.getPeeledObjectId());
                        } else {
                            log.add(ref.getObjectId());
                        }

                        Iterable<RevCommit> logs = log.call();
                        for (RevCommit rev : logs) {
                            System.out.println("Commit: " + rev.getName());
                        }


                    }

                }

            }

        }

        log.info("Ending");

    }
}
